package com.demo;

import com.demo.client.HbaseClient;
import org.junit.Test;

import java.io.IOException;

public class HbaseTest {


    @Test
    public void testHbase() throws IOException {

        String data = HbaseClient.getData("user", "1", "color", "red");
        System.out.println(data);
    }
}
