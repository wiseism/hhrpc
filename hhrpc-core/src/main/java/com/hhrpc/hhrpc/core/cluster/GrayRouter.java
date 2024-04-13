package com.hhrpc.hhrpc.core.cluster;

import com.google.common.collect.Lists;
import com.hhrpc.hhrpc.core.api.Router;
import com.hhrpc.hhrpc.core.meta.InstanceMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @Date 2024/4/13
 * @Author lifei
 */
public class GrayRouter implements Router<InstanceMeta> {

    private static final Logger log = LoggerFactory.getLogger(GrayRouter.class);

    private int grayRatio;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> rout(List<InstanceMeta> providers) {
        if (Objects.isNull(providers) || providers.size()<=1) {
            return providers;
        }
        List<InstanceMeta> grayInstanceList = Lists.newArrayList();
        List<InstanceMeta> normalInstanceList = Lists.newArrayList();
        for (InstanceMeta provider : providers) {
            if (Objects.nonNull(provider.getParameters())
                    && provider.getParameters().containsKey("gray")
                    && Boolean.parseBoolean(provider.getParameters().get("gray"))) {
                grayInstanceList.add(provider);
            }else {
                normalInstanceList.add(provider);
            }
        }
        log.debug("==> GrayRouter grayInstanceList/normalInstanceList, grayRatio: {}, {}, {}",
                grayInstanceList.size(), normalInstanceList.size(), grayRatio);
        if (grayInstanceList.isEmpty() || normalInstanceList.isEmpty()) {
            return providers;
        }
        if (grayRatio<=0) {
            return normalInstanceList;
        } else if (grayRatio>=100) {
            return grayInstanceList;
        }
        if (RANDOM.nextInt(100)<grayRatio) {
            return grayInstanceList;
        } else {
            return normalInstanceList;
        }
    }

    public void setGrayRatio(int grayRatio) {
        this.grayRatio = grayRatio;
    }
}
