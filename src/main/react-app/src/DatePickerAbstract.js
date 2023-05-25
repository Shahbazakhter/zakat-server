
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

const DatePickerAbstract = ({ startDate, endDate, setStartDate, setEndDate, handleFilterTransactions, handleZakat, handleInterest }) => {

    const handleZakatClick = (event) => {
        event.preventDefault();
        setStartDate(startDate);
        setEndDate(endDate);
        handleFilterTransactions();
        handleZakat();
    };

    const handleInterestClick = (event) => {
        event.preventDefault();
        setStartDate(startDate);
        setEndDate(endDate);
        handleFilterTransactions();
        handleInterest();
    };

    return (
        <div>
            <div style={{ display: "inline-block" }}>
                <label htmlFor="start-date">Start Date:</label>
                <DatePicker
                    id="start-date"
                    selected={startDate}
                    onChange={(date) => setStartDate(date)}
                    timeZone="UTC"
                />
            </div>
            <div style={{ display: "inline-block" }}>
                <label htmlFor="end-date">End Date:</label>
                <DatePicker
                    id="end-date"
                    selected={endDate}
                    onChange={(date) => setEndDate(date)}
                    timeZone="UTC"
                />
            </div>
            <button onClick={handleZakatClick} className="btn btn-primary">Calculate Zakat</button>
            &nbsp;&nbsp;&nbsp;
            <button onClick={handleInterestClick} className="btn btn-primary">Calculate Interest</button>
        </div>
    );
}
export default DatePickerAbstract;