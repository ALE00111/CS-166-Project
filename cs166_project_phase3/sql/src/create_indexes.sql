DROP INDEX IF EXISTS FlightNum_index;
DROP INDEX IF EXISTS PlaneID_index;
DROP INDEX IF EXISTS TechnicianID_index;
DROP INDEX IF EXISts ReservationID_index;

CREATE INDEX FlightNum_index on FlightINstance(FlightNumber);
CREATE INDEX PlaneID_index on MaintenanceRequest(PlaneID);
CREATE INDEX TechnicianID_index on Repairs(TechnicianID);
CREATE INDEX ReservationID_index on Reservation(ReservationID);