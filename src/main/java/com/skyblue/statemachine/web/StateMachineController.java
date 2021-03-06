package com.skyblue.statemachine.web;

import javax.annotation.Resource;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyblue.statemachine.config.ComplexForm.ComplexFormEvents;
import com.skyblue.statemachine.config.ComplexForm.ComplexFormStateMachineBuilder;
import com.skyblue.statemachine.config.ComplexForm.ComplexFormStates;
import com.skyblue.statemachine.config.Form.Form;
import com.skyblue.statemachine.config.Form.FormEvents;
import com.skyblue.statemachine.config.Form.FormStateMachineBuilder;
import com.skyblue.statemachine.config.Form.FormStates;
import com.skyblue.statemachine.config.Order.InMemoryStateMachinePersist;
import com.skyblue.statemachine.config.Order.Order;
import com.skyblue.statemachine.config.OrderSingle.OrderEvents;
import com.skyblue.statemachine.config.Order.OrderStateMachineBuilder;
import com.skyblue.statemachine.config.OrderSingle.OrderStates;

@RestController
@RequestMapping("/statemachine")
public class StateMachineController {
	
	@Autowired
	private StateMachine orderSingleMachine;

	
	@Autowired
	private OrderStateMachineBuilder orderStateMachineBuilder;
	
	@Autowired
	private FormStateMachineBuilder formStateMachineBuilder;
	
	@Autowired
	private ComplexFormStateMachineBuilder complexFormStateMachineBuilder;
	
	@Autowired
	private InMemoryStateMachinePersist inMemoryStateMachinePersist;
	
	@Resource(name="orderMemoryPersister")
	private StateMachinePersister<OrderStates, OrderEvents, String> orderMemorypersister;
	
	@Resource(name="orderRedisPersister")
	private StateMachinePersister<OrderStates, OrderEvents, String> orderRedisPersister;
	
	@Resource(name="orderPersister")
	private StateMachinePersister<OrderStates, OrderEvents, Order> persister;

	@Autowired
	private BeanFactory beanFactory;
	
	@RequestMapping("/testSingleOrderState")
	public void testSingleOrderState() throws Exception {

		// 创建流程
		orderSingleMachine.start();

		// 触发PAY事件
		orderSingleMachine.sendEvent(OrderEvents.PAY);

		// 触发RECEIVE事件
		orderSingleMachine.sendEvent(OrderEvents.RECEIVE);

//		Order order = new Order(orderId, "547568678", "广东省深圳市", "13435465465", "RECEIVE");
//		Message<OrderEvents> message = MessageBuilder.withPayload(OrderEvents.RECEIVE).setHeader("order", order).build();
//		stateMachine.sendEvent(message);

		// 获取最终状态
		System.out.println("最终状态：" + orderSingleMachine.getState().getId());
	}
	
	
	@RequestMapping("/testOrderState")
	public void testOrderState(String orderId) throws Exception {

		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		System.out.println(stateMachine.getId());

		// 创建流程
		stateMachine.start();

		// 触发PAY事件
		stateMachine.sendEvent(OrderEvents.PAY);

		// 触发RECEIVE事件
		//stateMachine.sendEvent(OrderEvents.RECEIVE);
		
		//用message传递数据
		Order order = new Order(orderId, "547568678", "广东省深圳市", "13435465465", "RECEIVE");
		Message<OrderEvents> message = MessageBuilder.withPayload(OrderEvents.RECEIVE).setHeader("order", order).setHeader("otherObj", "otherObjValue").build();
		stateMachine.sendEvent(message);

		// 获取最终状态
		System.out.println("最终状态：" + stateMachine.getState().getId());
	}
	
	@RequestMapping("/testFormState")
	public void testFormState() throws Exception {

		StateMachine<FormStates, FormEvents> stateMachine = formStateMachineBuilder.build(beanFactory);
		System.out.println(stateMachine.getId());

		// 创建流程
		stateMachine.start();

		stateMachine.sendEvent(FormEvents.WRITE);

		stateMachine.sendEvent(FormEvents.CONFIRM);

		stateMachine.sendEvent(FormEvents.SUBMIT);

		// 获取最终状态
		System.out.println("最终状态：" + stateMachine.getState().getId());
	}
	
	@RequestMapping("/testComplexFormState")
	public void testComplexFormState() throws Exception {

		StateMachine<ComplexFormStates, ComplexFormEvents> stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		System.out.println(stateMachine.getId());
		
		Form form1 = new Form();
		form1.setId("111");
		form1.setFormName(null);
		
		Form form2 = new Form();
		form2.setId("222");
		form2.setFormName("好的表单");
		
		Form form3 = new Form();
		form3.setId("333");
		form3.setFormName(null);

		// 创建流程
		System.out.println("-------------------form1------------------");
		
		stateMachine.start();
		Message message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form1).build();
		stateMachine.sendEvent(message);
		System.out.println("最终状态：" + stateMachine.getState().getId());
		
		System.out.println("-------------------form2------------------");
		stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form2).build();
		stateMachine.sendEvent(message);
		System.out.println("最终状态：" + stateMachine.getState().getId());
		
		System.out.println("-------------------form3------------------");
		stateMachine = complexFormStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		message = MessageBuilder.withPayload(ComplexFormEvents.WRITE).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		form3.setFormName("好的表单");
		message = MessageBuilder.withPayload(ComplexFormEvents.DEAL).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		message = MessageBuilder.withPayload(ComplexFormEvents.CHECK).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("当前状态：" + stateMachine.getState().getId());
		message = MessageBuilder.withPayload(ComplexFormEvents.SUBMIT).setHeader("form", form3).build();
		stateMachine.sendEvent(message);
		System.out.println("最终状态：" + stateMachine.getState().getId());
	}

	@RequestMapping("/testMemoryPersister")
	public void tesMemorytPersister(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		
		//发送PAY事件
		stateMachine.sendEvent(OrderEvents.PAY);
		Order order = new Order();
		order.setId(id);
		
		//持久化stateMachine
		orderMemorypersister.persist(stateMachine, order.getId());
	
	}
	
	@RequestMapping("/testMemoryPersisterRestore")
	public void testMemoryRestore(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		orderMemorypersister.restore(stateMachine, id);
		System.out.println("恢复状态机后的状态为：" + stateMachine.getState().getId());
	}
	
	@RequestMapping("/testRedisPersister")
	public void testRedisPersister(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		stateMachine.start();
		Order order = new Order();
		order.setId(id);
		//发送PAY事件
		Message<OrderEvents> message = MessageBuilder.withPayload(OrderEvents.PAY).setHeader("order", order).build();
		stateMachine.sendEvent(message);
		//持久化stateMachine
		orderRedisPersister.persist(stateMachine, order.getId());
	}
	
	@RequestMapping("/testRedisPersisterRestore")
	public void testRestore(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		orderRedisPersister.restore(stateMachine, id);
		System.out.println("恢复状态机后的状态为：" + stateMachine.getState().getId());
	}
	
	@RequestMapping("/testOrderRestore")
	public void testOrderRestore(String id) throws Exception {
		StateMachine<OrderStates, OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
		//订单
		Order order = new Order();
		order.setId(id);
		order.setState(OrderStates.WAITING_FOR_RECEIVE.toString());
		//恢复
		persister.restore(stateMachine, order);
		//查看恢复后状态机的状态
		System.out.println("恢复后的状态：" + stateMachine.getState().getId());
	}
}
