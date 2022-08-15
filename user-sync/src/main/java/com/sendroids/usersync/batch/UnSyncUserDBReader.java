package com.sendroids.usersync.batch;

import com.sendroids.usersync.entity.UserIdentity;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public interface UnSyncUserDBReader<ID> extends ItemReader<UserIdentity<ID>> {
    @Override
    default UserIdentity<ID> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return null;
    }
}
