package jpabook.helloshop.domain;

import javax.persistence.Embeddable;

@Embeddable
public enum DeliveryStatus {
    READY, COMP
}
