package com.it.soul.lab.unit.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.it.soul.lab.service.MailService;
import com.it.soul.lab.service.MailService.ContentType;
import com.it.soul.lab.service.MailService.MailServiceType;

public class MailServiceTest {
	
	private MailService service;
	
	@Before
	public void before() {
		service = new MailService.Builder(MailServiceType.SSL)
								.createAuth("itracker26@gmail.com", "...")
								.subject("IT SOUL LIMITED")
								.receivers("m.towhid.islam@gmail.com").build();
	}
	
	@After
	public void after() {
		service = null;
	}

	@Test
	public void testSendMailString() {
		service.sendMail("<h1>This is actual message</h1>", ContentType.HTML);
		Assert.assertTrue(true);
	}

	@Test
	public void testSendMailFile() {
		service.sendMail("This is actual message", ContentType.PLAIN);
		Assert.assertTrue(true);
	}

}
