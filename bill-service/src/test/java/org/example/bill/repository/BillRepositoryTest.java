package org.example.bill.repository;

import org.example.bill.entity.Bill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class BillRepositoryTest {

    @Autowired
    private BillRepository underTest;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void itShouldSaveBill() {
        // given
        Bill bill = getBill();

        // when
        underTest.save(bill);

        // then
        Optional<Bill> expected = underTest.findById(bill.getBillId());
        assertThat(expected.isPresent()).isTrue();
    }

    @Test
    void itShouldNotSaveBill_whenNullConstraintIsDisturb() {
        // given
        Bill bill = getBill();
        bill.setAccount(null);

        // when
        // then
        assertThatThrownBy(() -> entityManager.persistAndFlush(bill))
                .isInstanceOf(PersistenceException.class)
                .hasMessageContaining("could not execute statement");
    }

    @Test
    void itShouldFindByAccountId() {
        // given
        Bill bill = getBill();
        underTest.save(bill);

        // when
        List<Bill> expected = underTest.findByAccountId(bill.getAccount());

        // then
        assertThat(expected.size()).isEqualTo(1);
    }

    @Test
    void itShouldNotFindByAccountId_whenAccountIsNotExist() {
        // given
        Bill bill = getBill();
        underTest.save(bill);

        // when
        List<Bill> expected = underTest.findByAccountId(777L);

        // then
        assertThat(expected.size()).isEqualTo(0);
    }

    private static Bill getBill() {
        return Bill.builder()
                .amount(new BigDecimal(100))
                .account(1L)
                .creationDate(OffsetDateTime.now())
                .isDefault(true)
                .overdraftEnabled(true)
                .build();
    }
}