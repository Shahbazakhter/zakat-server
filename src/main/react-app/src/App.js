import logo from './logo.svg';
import './App.css';
import React, { useState } from 'react';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from './Table';


function App() {
  const [selectedFile, setSelectedFile] = useState(null);

    const handleFileInput = (e) => {
      setSelectedFile(e.target.files[0]);
    }

    const handleSubmit = (e) => {
      e.preventDefault();
      const formData = new FormData();
      formData.append('file', selectedFile);
      axios.post('http://localhost:8080/v1/statement/upload', formData)
        .then(res => {
          console.log(res.data);
        })
        .catch(err => {
          console.log("ERROR OCCORRED");
          console.error(err);
        });
    }

    return (
      <div className="container mt-5">
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="file">Choose Statements to Upload</label> <pre/>
          <input type="file" className="form-control-file" id="file" onChange={handleFileInput} /> <pre/>
        </div>
        <button type="submit" className="btn btn-primary">Upload</button>
      </form><pre/>
      <Table/>
    </div>
    );
}

export default App;
