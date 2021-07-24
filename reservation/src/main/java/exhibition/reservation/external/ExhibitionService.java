
package exhibition.reservation.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;


@FeignClient(name="exhibition", url="http://exhibition:8082")
public interface ExhibitionService {

    //@RequestMapping(method= RequestMethod.GET, path="/exhibitions")
    //public void getStatus(@RequestBody Exhibition exhibition);

    @RequestMapping(method= RequestMethod.GET, value="/exhibitions/{id}", consumes = "application/json")
    public Exhibition getstatus(@PathVariable("id") Long id);


}



//@FeignClient(name="resort", url="${feign.resort.url}")
//public interface ResortService {

    //@PatchMapping(path="/resorts/{id}")
    //@RequestMapping(method= RequestMethod.PATCH, path="/resorts/{id}")
    //public void resortStatusChange(@RequestParam("id") Long id, @RequestBody Resort resort);

   // @RequestMapping(method= RequestMethod.GET, value="/resorts/{id}", consumes = "application/json")
   // public Resort getResortStatus(@PathVariable("id") Long id);


