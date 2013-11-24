package vn.com.fpt.fti.api.helper;

import vn.com.fpt.fti.api.model.FailResponseJson;
import vn.com.fpt.fti.api.model.SuccessResponseJson;

public class ResponseHelper {
	public static SuccessResponseJson buildSuccessJsonReponse(String result){
		SuccessResponseJson json = new SuccessResponseJson();
		json.status = "succses";
		json.result = result;
		return json;
	}
	
	public static FailResponseJson buildFailJsonResponse(int errorCode) {
		FailResponseJson json = new FailResponseJson();
		json.status = "fail";
		json.errorCode = errorCode;
		json.errorMessage = getErrorMessage(errorCode);
		return json;
	}
	
	public static String getErrorMessage(int errorCode){
		String errorMessage = "";
		switch (errorCode){
			case 404:   errorMessage = "Not Found";
						break;
			case 411:   errorMessage = "Invalid Paramaters";
						break;
		}
		return errorMessage;
	}
}
