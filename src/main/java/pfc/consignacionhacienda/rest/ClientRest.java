package pfc.consignacionhacienda.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pfc.consignacionhacienda.dto.ClientDTO;
import pfc.consignacionhacienda.exceptions.client.ClientNotFoundException;
import pfc.consignacionhacienda.model.Client;
import pfc.consignacionhacienda.services.client.ClientService;

@RestController
@RequestMapping("/api/client")
public class ClientRest {

    @Autowired
    private ClientService clientService;

    private static final Logger logger = LoggerFactory.getLogger(ClientRest.class);

    @PostMapping
    ResponseEntity<Client> saveClient(@RequestBody Client newClient){
        try {
            return ResponseEntity.ok(clientService.saveClient(newClient));
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    ResponseEntity<Client> getClientById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(clientService.getClientById(id));
        }catch (ClientNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    ResponseEntity<Page<Client>> getClientById(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, name = "name", defaultValue = "") String clientName
    ){
        try {
            return ResponseEntity.ok(clientService.getClientsByPage(page, limit, clientName));
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Client> updateClientById(@PathVariable Integer id){
        try {
            return ResponseEntity.ok(clientService.deleteClientById(id));
        }catch (ClientNotFoundException e){
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PatchMapping("/{id}")
    ResponseEntity<Client> updateClientById(@RequestBody ClientDTO clientDTO, @PathVariable Integer id){
        try {
            return ResponseEntity.ok(clientService.updateClientById(clientDTO, id));
        } catch (ClientNotFoundException e){
            logger.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
