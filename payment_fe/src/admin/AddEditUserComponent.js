import axios from "axios";
import { Component } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import { host } from "..";
import DOMPurify from "dompurify";

class AddEditUserComponent extends Component {


    constructor(props) {
        super(props);
        this.state = {
            error: null,
            user: props.user
        }
        this.saveCallback = props.saveCallback;
        this.cancelCallback = props.cancelCallback;
    }
    componentDidUpdate(prevProps, prevState) {
        // console.log(prevProps)
        // console.log(prevState)
        // console.log(this.props)
        // console.log(this.state)
        if (this.props.user !== this.state.user) {
            this.setState({
                user: this.props.user
            })
        }
    }

    getUser() {
        return this.state.user;
    }
    handleStatus(event) {
        var user = this.getUser();
        user.merchant.active = event.target.checked;
        this.setState({
            item: user
        })
    }

    handleName(event) {
        var user = this.getUser();
        user.merchant.name = event.target.value;
        this.setState({
            item: user
        })
    }

    handleDescription(event) {
        var user = this.getUser();
        user.merchant.description = event.target.value;
        this.setState({
            item: user
        })
    }

    handleTransactionSum(event) {
        var user = this.getUser();
        user.merchant.totalTransactionSum = event.target.value;
        this.setState({
            item: user
        })
    }


    handleEmail(event) {
        var user = this.getUser();
        user.email = event.target.value;
        this.setState({
            item: user
        })
    }

    handlePassword(event) {
        var user = this.getUser();
        user.password = event.target.value;
        this.setState({
            item: user
        })
    }
    handleClose() {
        if (this.cancelCallback != null && this.cancelCallback instanceof Function) {
            this.cancelCallback()
        }
    }

    saveChanges() {
        axios.defaults.headers.common['Authorization'] = `Bearer ${localStorage.getItem("token")}`

        axios.put(host + '/admin/user', this.state.item)
            .then((response) => {
                if (response.data === "SUCCESS") {
                    if (this.saveCallback != null && this.saveCallback instanceof Function) {
                        this.saveCallback()
                    }
                } else if (response.data === "EMAIL_EXISTS") {

                    this.setState({
                        error: "Email already exists. Choose different one"
                    })
                } else {
                    this.setState({
                        error: DOMPurify.sanitize(response.data)
                    })
                }
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
            <div>
                {
                    this.state.user != null ? (
                        <Modal show={this.state.user != null} onHide={() => this.handleClose()}>
                            <Modal.Header closeButton>
                                <Modal.Title>Edit User</Modal.Title>
                            </Modal.Header>
                            <Modal.Body>
                                <Form>
                                    {this.state.user.merchant != null ? (
                                        <div>
                                            <Form.Check
                                                checked={this.state.user.merchant.active}
                                                onChange={(event) => this.handleStatus(event)}
                                                type="switch"
                                                id="is-active"
                                                label="Is Active"
                                            />
                                            <Form.Group className="mb-3" controlId="name"  >
                                                <Form.Label>Name</Form.Label>
                                                <Form.Control type="text" placeholder="Name" value={this.state.user.merchant.name} onChange={(e) => this.handleName(e)} />
                                            </Form.Group>

                                            <Form.Group className="mb-3" controlId="description">
                                                <Form.Label>Description</Form.Label>
                                                <Form.Control as="textarea" rows={3} type="text" placeholder="Description" value={this.state.user.merchant.description} onChange={(event) => this.handleDescription(event)} />
                                            </Form.Group>

                                            <Form.Group className="mb-3" controlId="totalTransactionSum"  >
                                                <Form.Label>Total Transaction Sum</Form.Label>
                                                <Form.Control type="number" placeholder="Total Transaction Sum" value={this.state.user.merchant.totalTransactionSum} onChange={(event) => this.handleTransactionSum(event)} />
                                            </Form.Group>
                                        </div>
                                    ) : ("")}
                                    <Form.Group className="mb-3" controlId="Email"  >
                                        <Form.Label>Email</Form.Label>
                                        <Form.Control type="email" placeholder="Email" value={this.state.user.email} onChange={(event) => this.handleEmail(event)} />
                                    </Form.Group>
                                    <Form.Group className="mb-3" controlId="Password"  >
                                        <Form.Label>Password</Form.Label>
                                        <Form.Control type="password" placeholder="Password" value={this.state.user.password} onChange={(event) => this.handlePassword(event)} />
                                    </Form.Group>
                                </Form>
                                {this.state.error != null ? this.state.error : ''}
                            </Modal.Body>
                            <Modal.Footer>
                                <Button variant="secondary" onClick={() => this.handleClose()}>
                                    Close
                                </Button>
                                <Button variant="primary" onClick={() => this.saveChanges()}>
                                    Save Changes
                                </Button>
                            </Modal.Footer>
                        </Modal>
                    ) : ("")
                }

            </div>
        )
    }
}

export default AddEditUserComponent