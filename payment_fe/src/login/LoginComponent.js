import axios from 'axios';
import { Component } from 'react';
import { Button, Card, CardBody, Container, Form, ProgressBar } from 'react-bootstrap';
import { host } from '..';


class LogInFormComponent extends Component {

  constructor(props) {
    super(props);
    this.state =
    {
      email: '',
      password: '',
      loading: false
    };

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
    this.setState({ loading: true })
    axios.post(host + '/public/login',
      {
        email: this.state.email,
        password: this.state.password
      }, { headers: { 'content-type': 'application/x-www-form-urlencoded' } })
      .then(function (response) {
        _this.setState({ loading: false })
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
        _this.setState({ loading: false })
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

                <Form.Control disabled={this.state.loading} type="email" placeholder="name@example.com" value={this.state.email} onChange={this.handleEmail} />
              </Form.Group>

              <Form.Group className="mb-3" controlId="password" value={this.state.password} onChange={this.handlePassword} >
                <Form.Label>Password</Form.Label>
                <Form.Control type="Password" placeholder="12345678" disabled={this.state.loading}/>
              </Form.Group>

              {this.state.loading ? <div><ProgressBar animated={true} min={0} max={100} now={50} />&nbsp;</div> : ''}


              <Button type='submit' disabled={this.state.loading}
              >Login</Button>
            </Form>
          </CardBody>
        </Card>
      </Container>
    )
  }
}
export default LogInFormComponent