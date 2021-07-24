
package exhibition.reservation.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="exhibition", url="http://exhibition:8080")
public interface ExhibitionService {
    @RequestMapping(method= RequestMethod.GET, path="/exhibitions")
    public void getStatus(@RequestBody Exhibition exhibition);

}

