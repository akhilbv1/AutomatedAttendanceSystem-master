package com.projects.automatedattendancesystem;

public interface AsyncListener {

    void onStart();

    void onComplete();

    void onShowMessage(String message);

    void doVisibilityOperations(int visibility);

    void onCompleteWithResult(Object result);

    void showToast(String message);
}
