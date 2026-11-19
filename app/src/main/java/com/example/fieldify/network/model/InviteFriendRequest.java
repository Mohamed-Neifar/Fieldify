package com.example.fieldify.network.model;

public class InviteFriendRequest {
    private String invitedEmail;

    public InviteFriendRequest() {
    }

    public InviteFriendRequest(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }
}