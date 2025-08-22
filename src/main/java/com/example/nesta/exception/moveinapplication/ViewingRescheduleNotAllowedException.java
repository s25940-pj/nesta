package com.example.nesta.exception.moveinapplication;

public class ViewingRescheduleNotAllowedException extends RuntimeException {
    public ViewingRescheduleNotAllowedException() {
        super("Cannot reschedule viewing because landlord has already made a decision");
    }
}
