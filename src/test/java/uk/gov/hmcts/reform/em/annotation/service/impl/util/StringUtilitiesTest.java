package uk.gov.hmcts.reform.em.annotation.service.impl.util;

import org.junit.Assert;
import org.junit.Test;
import uk.gov.hmcts.reform.em.annotation.service.util.StringUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StringUtilitiesTest {

    @Test
    public void convertValidLog() {
        String dangerousLogStr = "this %0d is \r an %0a apple \n .";
        String safeLogStr = "this  is  an  apple  .";
        Assert.assertNotEquals(dangerousLogStr, safeLogStr);
        Assert.assertEquals(safeLogStr, StringUtilities.convertValidLogString(dangerousLogStr));
    }

    @Test
    public void convertValidLogEmptyList() {
        Assert.assertNotNull(StringUtilities.convertValidLogString(new ArrayList<>()));
    }

    @Test
    public void convertValidLogNonEmptyList() {

        String dangerousLogStr = "this %0d is \r an %0a apple \n .";
        String dangerousLogStr2 = "this %0d is \r an %0a mango \n .";
        String safeLogStr = "this  is  an  apple  .";
        List<String> initialList = Arrays.asList(dangerousLogStr, dangerousLogStr2);

        List<String> sanitisedList = StringUtilities.convertValidLogString(initialList);

        Assert.assertEquals(initialList.size(), sanitisedList.size());
        Assert.assertEquals(safeLogStr, sanitisedList.get(0));
    }

    @Test
    public void convertValidLogEmptyListUUID() {
        Assert.assertNotNull(StringUtilities.convertValidLogUUID(new ArrayList<>()));
    }
}
