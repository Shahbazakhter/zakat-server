import axios from 'axios';
import React, { useImperativeHandle } from 'react';
import moment from 'moment';

const Zakat = React.forwardRef(({ startDate, endDate, setStartDate, setEndDate, handleFilterTransactions,
    setErrorMessage, setSuccessMessage }, ref) => {
    const calculateZakat = async (startDate, endDate) => {
        try {
            if (moment(startDate).isAfter(moment(endDate))) {
                setErrorMessage("Start Date cannot be after End Date");
                return;
            }
            const formattedStartDate = startDate ? moment(startDate).format('YYYY-MM-DDTHH:mm:ss.SSS[Z]').slice(0, 10) : null;
            const formattedEndDate = endDate ? moment(endDate).format('YYYY-MM-DDTHH:mm:ss.SSS[Z]').slice(0, 10) : null;
            const response = await axios.post('http://localhost:8080/v1/zakat', {
                fromDate: formattedStartDate,
                toDate: formattedEndDate,
                isZakat: true,
            });
            handleFilterTransactions({"isZakat":true});
            if (!response.data) {
                setSuccessMessage(<span>No Transactions found {formattedStartDate && <span>| Year: {formattedStartDate} to {formattedEndDate}</span>}</span>);
            }
            response.data && response.data.formattedAmount && setSuccessMessage(<span>Zakat Amount:&nbsp;
                <b>{response.data.formattedAmount}</b> {formattedStartDate && <span>| Year: {formattedStartDate} to {formattedEndDate}</span>}</span>);
        } catch (error) {
            console.error(error);
        }
    };

    useImperativeHandle(ref, () => ({
        calculateZakat,
    }));

    return (
        <div>
        </div>
    );
});

export default Zakat;