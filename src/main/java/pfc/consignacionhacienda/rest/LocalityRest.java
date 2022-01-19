package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.InternalServerException;
import pfc.consignacionhacienda.exceptions.locality.LocalityNotFoundException;
import pfc.consignacionhacienda.exceptions.user.InvalidCredentialsException;
import pfc.consignacionhacienda.model.Locality;
import pfc.consignacionhacienda.services.locality.LocalityService;
import pfc.consignacionhacienda.utils.ErrorResponse;

@RestController
@RequestMapping("/api/locality")
public class LocalityRest {

    private static final Logger logger = LoggerFactory.getLogger(LocalityRest.class);

    @Autowired
    private LocalityService localityService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getLocalityById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(localityService.getLocalityById(id));
        } catch (LocalityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllLocalities(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, name = "name", defaultValue = "") String localitySearchName){
        try {
            if(localitySearchName.isBlank()){
                return ResponseEntity.ok(localityService.getAllLocalitiesByPages(page,limit));
            }else{
                return ResponseEntity.ok(localityService.getLocalitiesByName(page, limit, localitySearchName));
            }
        } catch (InvalidCredentialsException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping()
    public ResponseEntity<?> createLocality(@RequestBody Locality newLocality){
        try {
            return ResponseEntity.ok(localityService.saveLocality(newLocality));
        } catch (BadHttpRequest e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLocality(@PathVariable Integer id, @RequestBody Locality newLocality){
        try {
            return ResponseEntity.ok(localityService.updateLocalityById(id, newLocality));
        }catch (LocalityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (BadHttpRequest e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocalityById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(localityService.deleteLocalityById(id));
        } catch (LocalityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (InternalServerException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
