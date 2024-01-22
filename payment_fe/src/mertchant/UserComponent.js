import { Component } from "react";
import { Button, Container } from "react-bootstrap";
import TransactionListComponent from "./TransactionListComponent";

class UserComponent extends Component {

    constructor(props){
        super(props)

        this.logout = this.logout.bind(this);
    }

    logout() {
        localStorage.removeItem("token");
        window.location = "/"
    }

    render() {
        return (
            <Container>
                <h1>Transactions</h1>
                <TransactionListComponent />
                <Button onClick={this.logout}>Logout</Button>
            </Container>
        )
    }
}

export default UserComponent