DROP INDEX IF EXISTS FlightNum_index;
DROP INDEX IF EXISTS PlaneID_index;
DROP INDEX IF EXISTS TechnicianID_index;
DROP INDEX IF EXISTS ReservationID_index;
DROP INDEX IF EXISTS Date_index;
DROP INDEX IF EXISTS FlightInstanceID_index;
DROP INDEX IF EXISTS CustomerID_index;
DROP INDEX IF EXISTS PlaneID_repair_index;

CREATE INDEX FlightNum_index ON FlightInstance(FlightNumber);
CREATE INDEX PlaneID_index ON MaintenanceRequest(PlaneID);
CREATE INDEX TechnicianID_index ON Repair(TechnicianID);
CREATE INDEX ReservationID_index ON Reservation(ReservationID);
CREATE INDEX Date_index ON FlightInstance(FlightDate);
CREATE INDEX FlightInstanceID_index ON Reservation(FlightInstanceID);
CREATE INDEX CustomerID_index ON Reservation(CustomerID);
CREATE INDEX PlaneID_repair_index ON Repair(PlaneID);