DROP INDEX IF EXISTS FlightNum_index;
DROP INDEX IF EXISTS PlaneID_index;
DROP INDEX IF EXISTS TechnicianID_index;
DROP INDEX IF EXISTS ReservationID_index;
DROP INDEX IF EXISTS CustomerID_index;


--optimizes queries for fligt number as a criteria
CREATE INDEX FlightNum_index ON FlightInstance(FlightNumber);

--optimizes queries for plane id as a criteria
CREATE INDEX PlaneID_index ON MaintenanceRequest(PlaneID);

--optimizes queries for technician id as a criteria
CREATE INDEX TechnicianID_index ON Repair(TechnicianID);

--optimizes queries for reservation id as a criteria
CREATE INDEX ReservationID_index ON Reservation(ReservationID);

--optimizes queries for customer id as a criteria
CREATE INDEX CustomerID_index ON Reservation(CustomerID);

