package library


import grails.rest.*
import grails.converters.*
import java.util.*
import java.text.*
import groovy.time.TimeCategory

class BookController {
	static responseFormats = ['json', 'xml']
	
    def books() { 
        def statusMap=["data": ""]
        def role=request.JSON?.role
        if(role=="student")
        {
            def availableBooks=Book.findAllByIsAvailable("true")
            statusMap?.data=availableBooks
        }
        else if(role=="admin")
        {
            def unreturnBooks=Book_register.findAllByReturn_time(null)
            def filteredBooks=unreturnBooks?.findAll{temp -> 
                def taken=temp.taken_time
                Date curdate=new Date()
                use(groovy.time.TimeCategory) {
                    def duration = (curdate - taken).days
                    if(duration>15)
                        return true
                    else
                        return false
                }
            }
            // statusMap.data=filteredBooks
            statusMap?.data = filteredBooks?.collect { book ->
                [id: book.id, taken_time: formatDateTime(book.taken_time), user:book.user, book:book.book]
            }
        }
        render statusMap as JSON
    }
    def fullbooks()
    {
        def statusMap=["role":"","data": ""]
        def role=request.JSON?.role
        def limit=request.JSON?.limit
        def offset=request.JSON?.offset
        def user_id=request.JSON?.user_id
        def allbooks=Book.list(max: limit, offset:offset)
        statusMap.data=allbooks
        statusMap.role=role
        render statusMap as JSON
    }
     
    private String formatDateTime(Date date) {
        def sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        sdf.format(date)
    }
}