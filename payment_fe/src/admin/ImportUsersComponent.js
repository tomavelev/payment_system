import axios from "axios";
import { Component } from "react";
import { Button } from "react-bootstrap";
import { host } from "..";

class ImportUsersComponent extends Component {


    constructor(props) {
        super(props);
        this.state = {
            loading: props.loading != null && props.loading,
            csvFile: null
        };
        this.callback = props.callback;
    }


    fileSelect(event) {
        if (event.target.files.length > 0) {
            //TODO validate file type
            this.setState({
                csvFile: event.target.files[0]
            })
        }
    }

    handleSubmit(event) {
        event.preventDefault();
        const formData = new FormData();
        formData.append("file", this.state.csvFile);

        axios.post(host + '/admin/importCsv', formData, {
            headers: {
                "content-type": "multipart/form-data",
                'Authorization': `Bearer ${localStorage.getItem("token")}`,
            },
        }).then((response) => {
            this.setState({ csvFile: null }, () => {
                document.getElementById('importUsersForm').reset()

                if (this.callback != null && this.callback instanceof Function) {
                    this.callback()
                }
            })
        })
            .catch(function (error) {
                //TODO show error
                console.log(error);
                if (error.response != null && error.response.status === 403) {
                    window.location = '/';
                }
            });
    }

    render() {
        return (
            <div className="card">
                <div className="card-body">
                    <form id="importUsersForm" onSubmit={(e) => this.handleSubmit(e)}>
                        {(this.state.csvFile != null) ?
                            <Button onClick={(e) => this.handleSubmit(e)}>
                                Import Users from CSV
                            </Button>
                            : <span>Import Users from CSV</span>
                        }
                        &nbsp;
                        <input type="file" onChange={(event) => this.fileSelect(event)} />
                    </form>
                </div></div>
        )
    }
}

export default ImportUsersComponent