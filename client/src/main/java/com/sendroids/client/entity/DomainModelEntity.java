package com.sendroids.client.entity;

import org.springframework.lang.Nullable;

public interface DomainModelEntity<ID, VERSION> {

    @Nullable
    ID getId();

    @Nullable
    VERSION getVersion();
}
