package cn.huimin.process.web.util;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.impl.javax.el.PropertyNotFoundException;

public class ExpectionUtils {
    public static ErrorInfo getConditionErrorMsg(Exception e) {
        String errorMsg = e.getMessage();
        e.printStackTrace();
        if (e instanceof ActivitiException) {
            e.printStackTrace();
            Throwable cause = e.getCause();
            ErrorInfo propertyNotFound = new ErrorInfo("", "20000");
            if (cause != null && cause instanceof PropertyNotFoundException) {
                if (cause != null) {
                    PropertyNotFoundException propertyNotFoundException = (PropertyNotFoundException) cause;
                    String localizedMessage = propertyNotFoundException.getLocalizedMessage();
                    errorMsg = getPropertyNotFoundException(localizedMessage);
                    propertyNotFound = new ErrorInfo("", "10000");
                    propertyNotFound.errorMsg = errorMsg;
                }

            } else if (e instanceof ActivitiObjectNotFoundException) {
                String localizedMessage = e.getLocalizedMessage();
                errorMsg = localizedMessage;
                propertyNotFound = new ErrorInfo("", "10002");
                propertyNotFound.errorMsg = errorMsg;
            } else {
                String localizedMessage = e.getLocalizedMessage();
                errorMsg = localizedMessage;
                propertyNotFound = new ErrorInfo("", "10003");
                propertyNotFound.errorMsg = errorMsg;
            }
            return propertyNotFound;
        } else {
            ErrorInfo noOutFlow = new ErrorInfo("", "10001");
            noOutFlow.errorMsg = errorMsg;
            return noOutFlow;
        }

    }

    private static String getPropertyNotFoundException(String str) {
        int beginIndex = str.indexOf("'");
        int endIndex = str.lastIndexOf("'");
        String substring = str.substring(beginIndex + 1, endIndex);
        return substring;
    }

    public static class ErrorInfo {
        public String errorMsg;
        public String errorCode;

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public ErrorInfo() {
            super();
        }

        public ErrorInfo(String errorMsg, String errorCode) {
            this.errorMsg = errorMsg;
            this.errorCode = errorCode;
        }


    }

}
