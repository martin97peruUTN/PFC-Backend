package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;
import pfc.consignacionhacienda.services.client.ClientService;
import pfc.consignacionhacienda.utils.ErrorResponse;

@RestController
@RequestMapping("/api/client")
public class ClientRest {

    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(ClientRest.class);

    @PostMapping
    ResponseEntity<?> saveClient(@RequestBody Client newClient){
        try {
            return ResponseEntity.ok(clientService.saveClient(newClient));
        } catch (BadHttpRequest e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getClientByPage(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(clientService.getClientById(id));
        }catch (ClientNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    ResponseEntity<?> getClientByPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, name = "name", defaultValue = "") String clientName
    ){
        try {
            return ResponseEntity.ok(clientService.getClientsByPage(page, limit, clientName));
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteClientById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(clientService.deleteClientById(id));
        }catch (ClientNotFoundException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    ResponseEntity<?> updateClientById(@RequestBody ClientDTO clientDTO, @PathVariable Integer id){
        try {
            return ResponseEntity.ok(clientService.updateClientById(clientDTO, id));
        } catch (ClientNotFoundException e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (BadHttpRequest badHttpRequest) {
            logger.error(badHttpRequest.getMessage());
            return new ResponseEntity<>(new ErrorResponse(badHttpRequest.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
