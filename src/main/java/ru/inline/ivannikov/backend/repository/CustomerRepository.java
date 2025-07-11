package ru.inline.ivannikov.backend.repository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.inline.ivannikov.backend.exception.EntityNotSavedException;
import ru.inline.ivannikov.backend.filter.CustomerFilter;
import ru.inline.ivannikov.backend.model.Customer;

import java.util.List;
import java.util.Optional;

import static jooqdata.Tables.CUSTOMERS;
import static jooqdata.Tables.LOTS;

@Repository
@RequiredArgsConstructor
public class CustomerRepository {
    private final DSLContext dsl;

    public Customer findByCustomerCode(String customerCode) {
        return dsl.selectFrom(CUSTOMERS)
                .where(CUSTOMERS.CUSTOMER_CODE.eq(customerCode))
                .fetchOptionalInto(Customer.class)
                .orElseThrow(() -> new EntityNotFoundException("Customer with code " + customerCode + " not found"));
    }

    public boolean existsByCustomerCode(String customerCode) {
        return dsl.fetchExists(
                dsl.selectFrom(CUSTOMERS)
                .where(CUSTOMERS.CUSTOMER_CODE.eq(customerCode))
        );
    }

    public List<Customer> findAll() {
        return dsl.selectFrom(CUSTOMERS)
                .fetchInto(Customer.class);
    }

    public Customer save(Customer customer) {
        var record = dsl.newRecord(CUSTOMERS,  customer);

        return dsl.insertInto(CUSTOMERS)
                .set(record)
                .onConflict(CUSTOMERS.CUSTOMER_CODE)
                .doUpdate()
                .set(record)
                .returning()
                .fetchOptionalInto(Customer.class)
                .orElseThrow(() -> new EntityNotSavedException("Customer not saved!"));
    }


    public void deleteCustomerByCode(String customerCode) {
        dsl.delete(CUSTOMERS)
                .where(CUSTOMERS.CUSTOMER_CODE.eq(customerCode))
                .execute();
    }


    public void deleteCustomerCascade(String customerCode) {
        // 1. Удаляем все лоты контрагента
        dsl.deleteFrom(LOTS)
                .where(LOTS.CUSTOMER_CODE.eq(customerCode))
                .execute();

        // 2. Обнуляем ссылки на родителя у дочерних контрагентов
        dsl.update(CUSTOMERS)
                .set(CUSTOMERS.CUSTOMER_CODE_MAIN, (String) null)
                .where(CUSTOMERS.CUSTOMER_CODE_MAIN.eq(customerCode))
                .execute();

        // 3. Удаляем самого контрагента
        dsl.deleteFrom(CUSTOMERS)
                .where(CUSTOMERS.CUSTOMER_CODE.eq(customerCode))
                .execute();
    }

    public Page<Customer> findCustomers(CustomerFilter filter, Pageable pageable) {
        Condition where = buildWhereClause(filter);

        long total = dsl.selectCount()
                .from(CUSTOMERS)
                .where(where)
                .fetchOne(0, Long.class);

        List<Customer> results = dsl.selectFrom(CUSTOMERS)
                .where(where)
                .orderBy(createSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getPageNumber() * pageable.getPageSize())
                .fetchInto(Customer.class);

        return new PageImpl<>(results, pageable, total);
    }

    private Condition buildWhereClause(CustomerFilter filter) {
        Condition condition = DSL.noCondition();
        if (filter != null) {
            if (filter.getName() != null && !filter.getName().isBlank()) {
                condition = condition.and(CUSTOMERS.CUSTOMER_NAME.containsIgnoreCase(filter.getName()));
            }
            if (filter.getInn() != null && !filter.getInn().isBlank()) {
                condition = condition.and(CUSTOMERS.CUSTOMER_INN.eq(filter.getInn()));
            }
            if (filter.getIsOrganization() != null) {
                condition = condition.and(CUSTOMERS.IS_ORGANIZATION.eq(filter.getIsOrganization()));
            }
        }
        return condition;
    }

    private List<SortField<?>> createSortFields(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    Field<?> field = CUSTOMERS.field(order.getProperty());
                    if (field == null) {
                        throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
                    }
                    return order.isAscending() ? field.asc() : field.desc();
                })
                .toList();
    }

    public List<Customer> getChildrenCustomers(String parentCustomerCode) {
        return dsl.selectFrom(CUSTOMERS)
                .where(CUSTOMERS.CUSTOMER_CODE_MAIN.eq(parentCustomerCode))
                .fetchInto(Customer.class);
    }


    public Optional<Customer> getParentCustomer(String childCustomerCode) {
        // Сначала получаем код родителя
        String parentCode = dsl.select(CUSTOMERS.CUSTOMER_CODE_MAIN)
                .from(CUSTOMERS)
                .where(CUSTOMERS.CUSTOMER_CODE.eq(childCustomerCode))
                .fetchOneInto(String.class);

        // Если родителя нет - возвращаем пустой Optional
        if (parentCode == null) {
            return Optional.empty();
        }

        // Получаем родительского контрагента по коду
        return dsl.selectFrom(CUSTOMERS)
                .where(CUSTOMERS.CUSTOMER_CODE.eq(parentCode))
                .fetchOptionalInto(Customer.class);
    }


}
