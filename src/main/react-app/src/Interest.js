
import axios from 'axios';
import moment from 'moment';
import React, { useImperativeHandle } from 'react';

const Interest = React.forwardRef(({ startDate, endDate, setStartDate, setEndDate, handleFilterTransactions, setErrorMessage, setSuccessMessage }, ref) => {
    const calculateInterest = async (startDate, endDate) => {
        try {
            if(moment(startDate).isAfter(moment(endDate))){
                setErrorMessage("Start Date cannot be after End Date");
                return;
            }
            const formattedStartDate = startDate ? moment(startDate).format('YYYY-MM-DDTHH:mm:ss.SSS[Z]').slice(0, 10) : null;
            const formattedEndDate = endDate ? moment(endDate).format('YYYY-MM-DDTHH:mm:ss.SSS[Z]').slice(0, 10) : null;
            const response = await axios.post('http://localhost:8080/v1/interest', {
                fromDate: formattedStartDate,
                toDate: formattedEndDate,
                isInterest: true,
            });
            handleFilterTransactions({"isInterest":true});
            response.data && response.data.formattedAmount && setSuccessMessage(<span>Interest Amount:&nbsp;
                <b>{response.data.formattedAmount}</b> {formattedStartDate && <span>| Year: {formattedStartDate} to {formattedEndDate}</span>}</span>);
        } catch (error) {
            console.error(error);
        }
    };

    useImperativeHandle(ref, () => ({
        calculateInterest,
    }));

    return (
        <div>
        </div>
    )
});

export default Interest;