package com.sendroids.usersyncclient.annotation;

import com.sendroids.usersyncclient.UserBatchConfig;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class BatchSyncUserConfigSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{UserBatchConfig.class.getName()};
    }
}
