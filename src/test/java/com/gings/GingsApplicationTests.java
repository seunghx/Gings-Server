package com.gings;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gings.dao.UserMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GingsApplicationTests {

    @Autowired
    private UserMapper userMapper;
    
	@Test
	public void test() {
	    ModelMapper modelMapper = new ModelMapper();
	}

}

