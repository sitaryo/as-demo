package com.sendroids.usersync.batch;

import com.sendroids.usersync.entity.UserIdentity;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public interface SyncedUserDBWriter<ID> extends ItemWriter<UserIdentity<ID>> {

    @Override
    default void write(List<? extends UserIdentity<ID>> list) throws Exception{
    }
}
