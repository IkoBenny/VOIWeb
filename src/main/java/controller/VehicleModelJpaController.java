/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.desktop.model.VehicleMake;
import com.desktop.model.VehicleModel;
import com.desktop.model.VehicleOfInterest;
import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import controller.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author benny
 */
public class VehicleModelJpaController implements Serializable {

    public VehicleModelJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(VehicleModel vehicleModel) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (vehicleModel.getVehicleOfInterestCollection() == null) {
            vehicleModel.setVehicleOfInterestCollection(new ArrayList<VehicleOfInterest>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            VehicleMake make = vehicleModel.getMake();
            if (make != null) {
                make = em.getReference(make.getClass(), make.getMake());
                vehicleModel.setMake(make);
            }
            Collection<VehicleOfInterest> attachedVehicleOfInterestCollection = new ArrayList<VehicleOfInterest>();
            for (VehicleOfInterest vehicleOfInterestCollectionVehicleOfInterestToAttach : vehicleModel.getVehicleOfInterestCollection()) {
                vehicleOfInterestCollectionVehicleOfInterestToAttach = em.getReference(vehicleOfInterestCollectionVehicleOfInterestToAttach.getClass(), vehicleOfInterestCollectionVehicleOfInterestToAttach.getLicensePlate());
                attachedVehicleOfInterestCollection.add(vehicleOfInterestCollectionVehicleOfInterestToAttach);
            }
            vehicleModel.setVehicleOfInterestCollection(attachedVehicleOfInterestCollection);
            em.persist(vehicleModel);
            if (make != null) {
                make.getVehicleModelCollection().add(vehicleModel);
                make = em.merge(make);
            }
            for (VehicleOfInterest vehicleOfInterestCollectionVehicleOfInterest : vehicleModel.getVehicleOfInterestCollection()) {
                VehicleModel oldModelOfVehicleOfInterestCollectionVehicleOfInterest = vehicleOfInterestCollectionVehicleOfInterest.getModel();
                vehicleOfInterestCollectionVehicleOfInterest.setModel(vehicleModel);
                vehicleOfInterestCollectionVehicleOfInterest = em.merge(vehicleOfInterestCollectionVehicleOfInterest);
                if (oldModelOfVehicleOfInterestCollectionVehicleOfInterest != null) {
                    oldModelOfVehicleOfInterestCollectionVehicleOfInterest.getVehicleOfInterestCollection().remove(vehicleOfInterestCollectionVehicleOfInterest);
                    oldModelOfVehicleOfInterestCollectionVehicleOfInterest = em.merge(oldModelOfVehicleOfInterestCollectionVehicleOfInterest);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findVehicleModel(vehicleModel.getModel()) != null) {
                throw new PreexistingEntityException("VehicleModel " + vehicleModel + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(VehicleModel vehicleModel) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            VehicleModel persistentVehicleModel = em.find(VehicleModel.class, vehicleModel.getModel());
            VehicleMake makeOld = persistentVehicleModel.getMake();
            VehicleMake makeNew = vehicleModel.getMake();
            Collection<VehicleOfInterest> vehicleOfInterestCollectionOld = persistentVehicleModel.getVehicleOfInterestCollection();
            Collection<VehicleOfInterest> vehicleOfInterestCollectionNew = vehicleModel.getVehicleOfInterestCollection();
            List<String> illegalOrphanMessages = null;
            for (VehicleOfInterest vehicleOfInterestCollectionOldVehicleOfInterest : vehicleOfInterestCollectionOld) {
                if (!vehicleOfInterestCollectionNew.contains(vehicleOfInterestCollectionOldVehicleOfInterest)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain VehicleOfInterest " + vehicleOfInterestCollectionOldVehicleOfInterest + " since its model field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (makeNew != null) {
                makeNew = em.getReference(makeNew.getClass(), makeNew.getMake());
                vehicleModel.setMake(makeNew);
            }
            Collection<VehicleOfInterest> attachedVehicleOfInterestCollectionNew = new ArrayList<VehicleOfInterest>();
            for (VehicleOfInterest vehicleOfInterestCollectionNewVehicleOfInterestToAttach : vehicleOfInterestCollectionNew) {
                vehicleOfInterestCollectionNewVehicleOfInterestToAttach = em.getReference(vehicleOfInterestCollectionNewVehicleOfInterestToAttach.getClass(), vehicleOfInterestCollectionNewVehicleOfInterestToAttach.getLicensePlate());
                attachedVehicleOfInterestCollectionNew.add(vehicleOfInterestCollectionNewVehicleOfInterestToAttach);
            }
            vehicleOfInterestCollectionNew = attachedVehicleOfInterestCollectionNew;
            vehicleModel.setVehicleOfInterestCollection(vehicleOfInterestCollectionNew);
            vehicleModel = em.merge(vehicleModel);
            if (makeOld != null && !makeOld.equals(makeNew)) {
                makeOld.getVehicleModelCollection().remove(vehicleModel);
                makeOld = em.merge(makeOld);
            }
            if (makeNew != null && !makeNew.equals(makeOld)) {
                makeNew.getVehicleModelCollection().add(vehicleModel);
                makeNew = em.merge(makeNew);
            }
            for (VehicleOfInterest vehicleOfInterestCollectionNewVehicleOfInterest : vehicleOfInterestCollectionNew) {
                if (!vehicleOfInterestCollectionOld.contains(vehicleOfInterestCollectionNewVehicleOfInterest)) {
                    VehicleModel oldModelOfVehicleOfInterestCollectionNewVehicleOfInterest = vehicleOfInterestCollectionNewVehicleOfInterest.getModel();
                    vehicleOfInterestCollectionNewVehicleOfInterest.setModel(vehicleModel);
                    vehicleOfInterestCollectionNewVehicleOfInterest = em.merge(vehicleOfInterestCollectionNewVehicleOfInterest);
                    if (oldModelOfVehicleOfInterestCollectionNewVehicleOfInterest != null && !oldModelOfVehicleOfInterestCollectionNewVehicleOfInterest.equals(vehicleModel)) {
                        oldModelOfVehicleOfInterestCollectionNewVehicleOfInterest.getVehicleOfInterestCollection().remove(vehicleOfInterestCollectionNewVehicleOfInterest);
                        oldModelOfVehicleOfInterestCollectionNewVehicleOfInterest = em.merge(oldModelOfVehicleOfInterestCollectionNewVehicleOfInterest);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = vehicleModel.getModel();
                if (findVehicleModel(id) == null) {
                    throw new NonexistentEntityException("The vehicleModel with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            VehicleModel vehicleModel;
            try {
                vehicleModel = em.getReference(VehicleModel.class, id);
                vehicleModel.getModel();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vehicleModel with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<VehicleOfInterest> vehicleOfInterestCollectionOrphanCheck = vehicleModel.getVehicleOfInterestCollection();
            for (VehicleOfInterest vehicleOfInterestCollectionOrphanCheckVehicleOfInterest : vehicleOfInterestCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This VehicleModel (" + vehicleModel + ") cannot be destroyed since the VehicleOfInterest " + vehicleOfInterestCollectionOrphanCheckVehicleOfInterest + " in its vehicleOfInterestCollection field has a non-nullable model field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            VehicleMake make = vehicleModel.getMake();
            if (make != null) {
                make.getVehicleModelCollection().remove(vehicleModel);
                make = em.merge(make);
            }
            em.remove(vehicleModel);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<VehicleModel> findVehicleModelEntities() {
        return findVehicleModelEntities(true, -1, -1);
    }

    public List<VehicleModel> findVehicleModelEntities(int maxResults, int firstResult) {
        return findVehicleModelEntities(false, maxResults, firstResult);
    }

    private List<VehicleModel> findVehicleModelEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(VehicleModel.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public VehicleModel findVehicleModel(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(VehicleModel.class, id);
        } finally {
            em.close();
        }
    }

    public int getVehicleModelCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<VehicleModel> rt = cq.from(VehicleModel.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
