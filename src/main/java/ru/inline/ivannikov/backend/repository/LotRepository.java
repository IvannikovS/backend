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
import ru.inline.ivannikov.backend.filter.LotFilter;
import ru.inline.ivannikov.backend.model.Customer;
import ru.inline.ivannikov.backend.model.Lot;

import java.util.List;
import java.util.Optional;

import static jooqdata.Tables.CUSTOMERS;
import static jooqdata.Tables.LOTS;

@Repository
@RequiredArgsConstructor
public class LotRepository {
    private final DSLContext dsl;

    public Lot findById(Long id) {
        return dsl.selectFrom(LOTS)
                .where(LOTS.LOT_ID.eq(id))
                .fetchOptionalInto(Lot.class)
                .orElseThrow(() -> new  EntityNotFoundException("Lot with id " + id + " not found!"));
    }

    public List<Lot> findAll() {
        return dsl.selectFrom(LOTS)
                .fetchInto(Lot.class);
    }

    public List<Lot> findByCustomerCode(String customerCode) {
        return dsl.selectFrom(LOTS)
                .where(LOTS.CUSTOMER_CODE.eq(customerCode))
                .fetchInto(Lot.class);
    }

    public Lot save(Lot lot) {
        var record = dsl.newRecord(LOTS, lot);

        return dsl.insertInto(LOTS)
                .set(record)
                .onConflict(LOTS.LOT_ID)
                .doUpdate()
                .set(record)
                .returning()
                .fetchOptionalInto(Lot.class)
                .orElseThrow(() -> new EntityNotSavedException("Lot not saved!"));
    }

    public void deleteById(Long id) {
        dsl.deleteFrom(LOTS)
                .where(LOTS.LOT_ID.eq(id))
                .execute();
    }

    public Page<Lot> findLots(LotFilter  filter, Pageable pageable) {
        Condition where = buildWhereClause(filter);

        long total = dsl.selectCount()
                .from(LOTS)
                .where(where)
                .fetchOne(0, Long.class);

        List<Lot> result = dsl.selectFrom(LOTS)
                .where(where)
                .orderBy(createSortFields(pageable))
                .limit(pageable.getPageSize())
                .offset(pageable.getPageNumber() * pageable.getPageSize())
                .fetchInto(Lot.class);

        return new PageImpl<>(result, pageable, total);
    }

    private Condition buildWhereClause(LotFilter filter) {
        Condition condition = DSL.noCondition();
        if (filter != null) {
            if (filter.getLotName() != null && !filter.getLotName().isBlank()) {
                condition = condition.and(LOTS.LOT_NAME.containsIgnoreCase(filter.getLotName()));
            }
            if (filter.getCustomerCode() != null && !filter.getCustomerCode().isBlank()) {
                condition = condition.and(LOTS.CUSTOMER_CODE.eq(filter.getCustomerCode()));
            }
            if (filter.getMinPrice() != null) {
                condition = condition.and(LOTS.PRICE.ge(filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                condition = condition.and(LOTS.PRICE.le(filter.getMaxPrice()));
            }
            if (filter.getStartDate() != null) {
                condition = condition.and(LOTS.DATE_DELIVERY.ge(filter.getStartDate()));
            }
            if (filter.getEndDate() != null) {
                condition = condition.and(LOTS.DATE_DELIVERY.le(filter.getEndDate()));
            }
        }
        return condition;
    }

    private List<SortField<?>> createSortFields(Pageable pageable) {
        return pageable.getSort().stream()
                .map(order -> {
                    Field<?> field = LOTS.field(order.getProperty());
                    if (field == null) {
                        throw new IllegalArgumentException("Invalid sort property: " + order.getProperty());
                    }
                    return order.isAscending() ? field.asc() : field.desc();
                })
                .toList();
    }

    public Optional<Customer> getCustomerForLot(Long lotId) {
        return dsl.select(CUSTOMERS.fields())
                .from(LOTS)
                .join(CUSTOMERS).on(LOTS.CUSTOMER_CODE.eq(CUSTOMERS.CUSTOMER_CODE))
                .where(LOTS.LOT_ID.eq(lotId))
                .fetchOptionalInto(Customer.class);
    }

}
