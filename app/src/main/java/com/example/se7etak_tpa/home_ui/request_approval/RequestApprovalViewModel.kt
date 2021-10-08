package com.example.se7etak_tpa.home_ui.request_approval

import com.example.se7etak_tpa.home_ui.RequestApprovalAbstrarct.RequestApprovalAbstractClassViewModel
import com.example.se7etak_tpa.network.BASE_URL

class RequestApprovalViewModel : RequestApprovalAbstractClassViewModel(){
    init {
        API_SUBMISSION_URL = "${BASE_URL}api/Provider/SubmitApprovals"
    }
}