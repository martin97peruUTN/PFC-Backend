package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.InternalServerException;
import pfc.consignacionhacienda.exceptions.locality.LocalityNotFoundException;
import pfc.consignacionhacienda.model.Locality;
import pfc.consignacionhacienda.services.locality.LocalityService;

import java.util.List;

@RestController
@RequestMapping("/api/locality")
public class LocalityRest {

    private static final Logger logger = LoggerFactory.getLogger(LocalityRest.class);

    @Autowired
    private LocalityService localityService;

    @GetMapping("/{id}")
    public ResponseEntity<Locality> getLocalityById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(localityService.getLocalityById(id));
        } catch (LocalityNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<Page<Locality>> getAllLocalities(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit){
        try {
            return ResponseEntity.ok(localityService.getAllLocalitiesByPages(page,limit));
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping()
    public ResponseEntity<Locality> createLocality(@RequestBody Locality newLocality){
        try {
            return ResponseEntity.ok(localityService.saveLocality(newLocality));
        } catch (BadHttpRequest e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Locality> updateLocality(@PathVariable Integer id, @RequestBody Locality newLocality){
        try {
            return ResponseEntity.ok(localityService.updateLocalityById(id, newLocality));
        }catch (LocalityNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadHttpRequest e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Locality> deleteLocalityById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(localityService.deleteLocalityById(id));
        } catch (LocalityNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (InternalServerException e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
