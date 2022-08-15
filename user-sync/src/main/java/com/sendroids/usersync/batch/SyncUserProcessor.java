package com.sendroids.usersync.batch;


import com.sendroids.usersync.entity.UserIdentity;
import org.springframework.batch.item.ItemProcessor;

public interface SyncUserProcessor<ID> extends ItemProcessor<UserIdentity<ID>, UserIdentity<ID>> {
    @Override
    default UserIdentity<ID> process(UserIdentity<ID> idUserIdentity) throws Exception{
        return idUserIdentity;
    }
}
