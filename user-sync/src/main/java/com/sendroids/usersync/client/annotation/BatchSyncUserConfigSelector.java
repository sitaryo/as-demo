package com.sendroids.usersync.client.annotation;

import com.sendroids.usersync.client.UserBatchConfig;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class BatchSyncUserConfigSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{UserBatchConfig.class.getName()};
    }
}
