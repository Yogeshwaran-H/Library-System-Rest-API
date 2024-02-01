package library


import grails.rest.*
import grails.converters.*
import java.util.*
import java.text.*

import grails.gorm.transactions.Transactional

@Transactional

class Book_registerController {
	static responseFormats = ['json', 'xml']
	
    def modifyregister() {
        def statusMap=["message":"","data":""]
        def id=request.JSON?.bookregister_id 
        Date curdate=new Date()
        def returnTime= curdate
        def bookRegisterInstance=Book_register.get(id)
        if(bookRegisterInstance && bookRegisterInstance.return_time==null)
        {
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            // Date returnTime =sdf.parse(return_time)
            use(groovy.time.TimeCategory)
            {
                bookRegisterInstance.return_time=returnTime
                bookRegisterInstance.save()
                statusMap.message="successfully updated"
            }
            // statusMap?.data=bookRegisterInstance
            statusMap?.data = bookRegisterInstance?.collect { temp ->
                [id: temp.id, taken_time: formatDateTime(temp.taken_time), return_time :formatDateTime(temp.return_time), user_id : temp.user.id, book_id : temp.book.id]
            }

            def book_id=bookRegisterInstance.book.id
            def bookInstance=Book.get(book_id)
            bookInstance.isAvailable="true"
            bookInstance.save()
        }
        else if(bookRegisterInstance){
            statusMap.message="already submitted"
        }
        else{
            statusMap?.message="id is wrong"
        }
        render statusMap as JSON
    }
    @Transactional
    def newregister()
    {
        def statusMap=["message":"","data":""]
        def rs=request.JSON
        def bookIdIns=Book.get(rs.book_id)
        def bookRegisterIns=Book_register.findAllByBook(bookIdIns)
        def bookreturned=bookRegisterIns?.findAll{ temp ->
            def taken=temp?.return_time;
            if(taken==null)
            {
                return true
            }
        }
        if(!bookreturned)
        {
            def userIns=User.get(rs.user_id)
            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            // Date takenTime =sdf.parse(rs.taken_time)
            Date curdate=new Date();
            def takenTime=curdate
            def bookregisterIns= new Book_register(
                taken_time:takenTime,
                return_time:null,
                user:userIns,
                book:bookIdIns
            )
            if(bookregisterIns?.validate())
            {
                bookregisterIns.save()
                def bookid=bookregisterIns.book.id
                def bookupdateIns=Book.get(bookid)
                bookupdateIns.isAvailable="false"
                bookupdateIns.save()
                statusMap.message="successfully registered"
                // statusMap.data=bookregisterIns
                statusMap?.data = bookregisterIns?.collect { temp ->
                    [id: temp.id, taken_time: formatDateTime(temp.taken_time), user_id : temp.user.id, book_id : temp.book.id]
                }
            }
            else{
                println bookregisterIns.errors
            }
        }
        else{
            statusMap.message="already registered"
        }
        render statusMap as JSON
    }

    private String formatDateTime(Date date) {
        def sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        sdf.format(date)
    }
}
