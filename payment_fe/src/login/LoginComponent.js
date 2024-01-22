import axios from 'axios';
import { Component } from 'react';
import { Button, Card, CardBody, Container, Form } from 'react-bootstrap';
import { host } from '..';
import LoginService from '../service/LoginService';


class LogInFormComponent extends Component {

  constructor(props) {
    super(props);
    this.state = { email: '', password: '' };

    this.handleEmail = this.handleEmail.bind(this);
    this.handlePassword = this.handlePassword.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleEmail(event) {
    this.setState({ email: event.target.value });
  }
  handlePassword(event) {
    this.setState({ password: event.target.value });
  }


  redirect(path) {
    // history.pushState({},'','/')
    window.location = path;

    // this.props.navigation.navigation(path)
  }

  handleSubmit(event) {
    event.preventDefault();
    var _this = this;
    axios.post(host + '/public/login',
      {
        // email: this.state.email,
        // password: this.state.password
      }, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
      .then(function (response) {
        if (response.data.code === "SUCCESS") {
          localStorage.setItem("token", response.data.token);
          switch (response.data.role) {
            case 'ADMIN':
              _this.redirect("/admin")
              break;
            case 'MERCHANT':
              _this.redirect("/user")
              break;

            default:
              break;
          }
        }

      })
      .catch(function (error) {
        //TODO show error
        console.log(error);
      });

  }

  render() {
    return (
      <Container>
        <h1>Login to Payment System</h1>
        <Card>
          <CardBody>
            <Form onSubmit={this.handleSubmit}>

              <Form.Group className="mb-3" controlId="email">
                <Form.Label>Email address</Form.Label>
                {/* <input type="text" value={this.state.value} onChange={this.handleChange} /> */}

                <Form.Control type="email" placeholder="name@example.com" value={this.state.email} onChange={this.handleEmail} />
              </Form.Group>

              <Form.Group className="mb-3" controlId="password" value={this.state.password} onChange={this.handlePassword} >
                <Form.Label>Password</Form.Label>
                <Form.Control type="Password" placeholder="12345678" />
              </Form.Group>

              <Button type='submit'>Login</Button>
            </Form>
          </CardBody>
        </Card>
      </Container>
    )
  }
}
export default LogInFormComponent