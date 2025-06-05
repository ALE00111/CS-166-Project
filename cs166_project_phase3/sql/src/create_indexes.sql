DROP INDEX IF EXISTS FlightNum_index;
DROP INDEX IF EXISTS PlaneID_index;
DROP INDEX IF EXISTS TechnicianID_index;
DROP INDEX IF EXISTS ReservationID_index;
DROP INDEX IF EXISTS ;

CREATE INDEX FlightNum_index on FlightInstance(FlightNumber);
CREATE INDEX PlaneID_index on MaintenanceRequest(PlaneID);
CREATE INDEX TechnicianID_index on Repairs(TechnicianID);
CREATE INDEX ReservationID_index on Reservation(ReservationID);
CREATE INDEX 