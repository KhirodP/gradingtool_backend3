package com.pwc.grading.reportingdb.model;

/**
 * A POJO which is having the details of the JOB table.
 *
 */
public class Job {

	private String jobId;
	
	private String operationType;
	
	private String jsonObj;
	
	private String createdTime;
	
	private String startTime;
	
	private String endTime;
	
	private String status;
	
	private String failureMessage;
	
	private int retryCount;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJsonObj() {
		return jsonObj;
	}

	public void setJsonObj(String jsonObj) {
		this.jsonObj = jsonObj;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFailureMessage() {
		return failureMessage;
	}

	public void setFailureMessage(String failureMessage) {
		this.failureMessage = failureMessage;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	@Override
	public String toString() {
		return "Job [jobId=" + jobId + ", operationType=" + operationType + ", createdTime=" + createdTime
				+ ", startTime=" + startTime + ", endTime=" + endTime + ", status=" + status + ", failureMessage="
				+ failureMessage + ", retryCount=" + retryCount + "]";
	}





	
	
}
