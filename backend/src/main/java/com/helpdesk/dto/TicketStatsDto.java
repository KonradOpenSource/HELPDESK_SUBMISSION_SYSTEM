package com.helpdesk.dto;

public class TicketStatsDto {
    private Long total;
    private Long newTickets;
    private Long inProgress;
    private Long resolved;
    private Long closed;
    private Long myTickets;
    private Long assignedToMe;

    public TicketStatsDto() {}

    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }

    public Long getNewTickets() { return newTickets; }
    public void setNewTickets(Long newTickets) { this.newTickets = newTickets; }

    public Long getInProgress() { return inProgress; }
    public void setInProgress(Long inProgress) { this.inProgress = inProgress; }

    public Long getResolved() { return resolved; }
    public void setResolved(Long resolved) { this.resolved = resolved; }

    public Long getClosed() { return closed; }
    public void setClosed(Long closed) { this.closed = closed; }

    public Long getMyTickets() { return myTickets; }
    public void setMyTickets(Long myTickets) { this.myTickets = myTickets; }

    public Long getAssignedToMe() { return assignedToMe; }
    public void setAssignedToMe(Long assignedToMe) { this.assignedToMe = assignedToMe; }
}
