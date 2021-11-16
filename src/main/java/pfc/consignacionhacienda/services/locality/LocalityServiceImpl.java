package pfc.consignacionhacienda.services.locality;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pfc.consignacionhacienda.dao.LocalityDAO;
import pfc.consignacionhacienda.exceptions.BadHttpRequest;
import pfc.consignacionhacienda.exceptions.locality.LocalityNotFoundException;
import pfc.consignacionhacienda.model.Locality;

import java.util.List;
import java.util.Optional;

@Service
public class LocalityServiceImpl implements  LocalityService{

    @Autowired
    LocalityDAO localityDAO;
    @Override
    public Locality getLocalityById(Integer id) throws LocalityNotFoundException {
        Optional<Locality> localityOpt = localityDAO.findById(id);
        if(localityOpt.isPresent()){
            return localityOpt.get();
        }
        throw new LocalityNotFoundException("No existe localidad con id: " + id);
    }

    @Override
    public List<Locality> getAllAvailablesLocalities() {
        return localityDAO.findByDeletedNotNullAndDeletedFalse();
    }

    @Override
    public Page<Locality> getAllLocalitiesByPages(Integer pageNumber, Integer limit) {
        return localityDAO.findByDeletedNotNullAndDeletedFalse(PageRequest.of(pageNumber,limit));
    }

    @Override
    public List<Locality> getAllLocalities() {
        return localityDAO.findAll();
    }

    @Override
    public Locality saveLocality(Locality locality) throws BadHttpRequest {
        if(locality.getName()==null){
            throw new BadHttpRequest("El parametro name no puede ser nulo");
        }
        return localityDAO.save(locality);
    }

    @Override
    public Locality updateLocalityById(Integer id, Locality locality) throws LocalityNotFoundException, BadHttpRequest {
        if(locality.getId() != null && !locality.getId().equals(id)){
            throw new BadHttpRequest("El parametro {id} no coincide con el id de la localidad que se esta por modificar.");
        }
        Locality l = getLocalityById(id);
        if(l.isDeleted()){
            throw new LocalityNotFoundException("La localidad con id: " + id + " no existe");
        }
        l.setName(locality.getName());
        return saveLocality(l);
    }

    @Override
    public Locality deleteLocalityById(Integer id) throws LocalityNotFoundException {
        Locality l = getLocalityById(id);
        if(l.isDeleted()){
            throw new LocalityNotFoundException("La localidad con id: " + id + " no existe");
        }
        l.setDeleted(true);
        return localityDAO.save(l);
    }

    @Override
    public Page<Locality> getLocalitiesByName(Integer pageNumber, Integer limit, String localitySearchName) {
        return localityDAO.findByDeletedNotNullAndDeletedFalseAndNameContaining(PageRequest.of(pageNumber,limit), localitySearchName);
    }
}
