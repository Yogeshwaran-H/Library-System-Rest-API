package library


import grails.rest.*
import grails.converters.*

class UserController {
	static responseFormats = ['json', 'xml']
	
    def login() {
        def statusMap=["message":"","status":"","role":""]
        def userName=request.JSON.userName
        def passWord=request.JSON.passWord
        def encryptedPassWord=getEncryp(passWord)
        def userDetail=User.findByUserName(userName)
        if(userDetail && userDetail.passWord==encryptedPassWord)
        {
            statusMap?.message="logged in successfully"
            statusMap?.status=true
            statusMap?.role=userDetail.role
        }
        else if(userDetail && userDetail.passWord!=passWord)
        {
            statusMap?.message="incorrect password"
            statusMap?.status=false
        }
        else{ 
            statusMap?.message="user didn't exist"
            statusMap?.status=false
        }
        respond statusMap
    }

    static String getEncryp(String input) {
    def encryp = ""
    for(int i=0; i<input.length(); i++) {
        encryp += (char)((int)input.charAt(i) + (i+1) * 5)
    }
    return encryp
    }

}