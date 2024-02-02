import { Component } from "react";
import { Button, Form, Modal } from "react-bootstrap";
import { host } from "..";
import axios from "axios";
import DOMPurify from "dompurify";

class AddTransactionComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            transaction: props.transaction,
            loading: false,
            referencedTransactions: [
            ]
        }
        this.saveCallback = props.saveCallback;
        this.cancelCallback = props.cancelCallback;
    }

    load() {
        axios.get(host + '/transactions?offset=0&limit=100',//paging should be broat here
            { headers: { 'Authorization': 'Bearer ' + localStorage.getItem("token") } })
            .then((response) => {

                this.setState({
                    referenedTransactions: response.data.list
                })
            })
            .catch((error) => {
                //TODO show error
                console.log(error);
                if (error.response != null && error.response.status === 403) {
                    window.location = '/';
                }
            });
    }
    componentDidUpdate(prevProps, prevState) {
        if (this.props.transaction !== this.state.transaction) {
            this.setState({
                transaction: this.props.transaction
            })
            this.load();
        }
    }

    handleClose() {
        if (this.cancelCallback != null && this.cancelCallback instanceof Function) {
            this.cancelCallback()
        }
    }

    saveChanges() {
        axios.defaults.headers.common['Authorization'] = `Bearer ${localStorage.getItem("token")}`

        this.setState({ loading: true })
        axios.post(host + '/transactions', this.state.transaction)
            .then((response) => {
                this.setState({ loading: false })


                if (response.data.code === "SUCCESS") {
                    if (this.saveCallback != null && this.saveCallback instanceof Function) {
                        this.saveCallback()
                    }
                } else if (response.data.code === "MERCHANT_NOT_ACTIVE") {

                    this.setState({
                        error: "Your profile is not Active!"
                    })
                } else {
                    this.setState({
                        error: DOMPurify.sanitize(response.data.message)
                    })
                }
            })
            .catch((error) => {
                this.setState({ loading: false })
                //TODO show error
                console.log(error);
                if (error.response != null && error.response.status === 403) {
                    window.location = '/';
                }
            });
    }



    getTransaction() {
        return this.state.transaction;
    }
    handleCustomerEmail(event) {
        var transaction = this.getTransaction();
        transaction.customerEmail = event.target.value;
        this.setState({
            transaction: transaction
        })
    }
    handleCustomerPhone(event) {
        var transaction = this.getTransaction();
        transaction.customerPhone = event.target.value;
        this.setState({
            transaction: transaction
        })
    }
    handleAmount(event) {
        var transaction = this.getTransaction();
        transaction.amount = event.target.value;
        this.setState({
            transaction: transaction
        })
    }
    handleStatus(event) {
        var transaction = this.getTransaction();
        transaction.status = event.target.value;
        this.setState({
            transaction: transaction
        })
    }
    handleReference(event) {
        var transaction = this.getTransaction();
        transaction.referenceId = event.target.value;
        this.setState({
            transaction: transaction
        })
    }

    render() {
        return (
            <div>
                {this.state.transaction != null ? (
                    <Modal show={this.state.transaction != null} onHide={() => this.handleClose()}>
                        <Modal.Header closeButton>
                            <Modal.Title>Edit Transaction</Modal.Title>
                        </Modal.Header>
                        <Modal.Body>
                            <Form>
                                <Form.Group className="mb-3" controlId="uuid"  >
                                    <Form.Label>UUID</Form.Label>
                                    <Form.Control type="text" placeholder="Customer Email" disabled="disabled" value={this.state.transaction.uuid}
                                    />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="customerEmail"  >
                                    <Form.Label>Customer Email</Form.Label>
                                    <Form.Control disabled={this.state.loading} type="email" placeholder="Customer Email" value={this.state.transaction.customerEmail}
                                        onChange={(event) => this.handleCustomerEmail(event)} />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="customerPhone"  >
                                    <Form.Label>Customer Phone</Form.Label>
                                    <Form.Control disabled={this.state.loading} type="phone" placeholder="Customer Phone" value={this.state.transaction.customerPhone}
                                        onChange={(event) => this.handleCustomerPhone(event)} />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="amount">
                                    <Form.Label>Amount</Form.Label>
                                    <Form.Control disabled={this.state.loading} type="number" placeholder="Amount" value={this.state.transaction.amount}
                                        onChange={(event) => this.handleAmount(event)} />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="Status"  >
                                    <Form.Label>Status</Form.Label>
                                    <Form.Select onChange={(event) => this.handleStatus(event)} disabled={this.state.loading}>
                                        <option value="APPROVED">Approved</option>
                                        <option value="REVERSED">Reversed</option>
                                        <option value="REFUNDED">Refunded</option>
                                        <option value="ERROR">Error</option>
                                    </Form.Select>
                                </Form.Group>

                                <Form.Group className="mb-3" controlId="referenceId"  >
                                    <Form.Label>Reference Id</Form.Label>
                                    <Form.Select onChange={(event) => this.handleReference(event)} disabled={this.state.loading}>
                                        {this.state.referencedTransactions.map((e, key) => {
                                            return <option key={e.uuid} value={e.id}>{e.status} {e.amount} {new Date(e.createdAt).toUTCString()}</option>;
                                        })}
                                    </Form.Select>
                                </Form.Group>

                            </Form>
                            <pre>{this.state.error != null ?this.state.error : ''}</pre>
                        </Modal.Body>
                        <Modal.Footer>
                            <Button variant="secondary" onClick={() => this.handleClose()}>
                                Close
                            </Button>
                            <Button variant="primary" onClick={() => this.saveChanges()} disabled={this.state.loading}>
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

export default AddTransactionComponent