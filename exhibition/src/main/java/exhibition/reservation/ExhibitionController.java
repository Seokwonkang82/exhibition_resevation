package exhibition.reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

 @RestController
 public class ExhibitionController {

    private ExhibitionRepository repository;
    public ExhibitionController(ExhibitionRepository repository){
        this.repository = repository;
    }

   // getExhibitionStatus get 호출 시 400밀리초 ~ 620밀리초의 지연시간 발생시킴
    @RequestMapping(method= RequestMethod.GET, value="/exhibitions/{id}", consumes = "application/json")
        public Exhibition getExhibitionStatus(@PathVariable("id") Long id){
            //hystix test code
             try {
                 Thread.currentThread().sleep((long) (400 + Math.random() * 220)); // (+ 0~1*220)
             } catch (InterruptedException e) { }
             return repository.findById(id).get();
         }
 }
