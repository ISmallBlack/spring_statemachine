package com.skyblue.statemachine.config.ComplexForm;

import com.skyblue.statemachine.config.Form.Form;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;

/**
 * DEAL_CHOICE guard
 * @author wphmoon
 *
 */
public class ComplexFormDealChoiceGuard implements Guard<ComplexFormStates, ComplexFormEvents> {

	@Override
	public boolean evaluate(StateContext<ComplexFormStates, ComplexFormEvents> context) {
		System.out.println("ComplexFormDealChoiceGuard!!!!!!!!!!!!!");
		boolean returnValue = false;
		Form form = context.getMessage().getHeaders().get("form", Form.class);
		
		if ((form.getFormName() == null)||(form.getFormName().indexOf("坏") > -1)) {
			returnValue = false;
		} else {
			returnValue = true;
		}
		
		System.out.println(form.toString()+" is "+returnValue);
		return returnValue;
	}

}
