import { Component } from "react";
import { Button } from "react-bootstrap";

class LogoutButton extends Component {
    logout() {
        localStorage.removeItem("token");
        window.location = "/"
    }
    render() {
        return (
            <Button onClick={this.logout}>Logout</Button>
        )
    }
}
export default LogoutButton