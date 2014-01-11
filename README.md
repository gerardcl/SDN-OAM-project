SDN-OAM-project
===============

<h2>REST API Paths</h2>
<h3>Topology</h3>
<p>Resources availables at 'dxat.appserver.topology.resources'</p>

| URL                                         | Description                                             |
| ------------------------------------------- |:-------------------------------------------------------:|
| AppServer/webapi/topology/switches/         | Return all switches (enabled and disabled)              |
| AppServer/webapi/topology/switches/{dpid}   | Return the switch with the desired dpid                 |
| AppServer/webapi/topology/links             | Return all the links (enabled and disabled              |
| AppServer/webapi/topology/links/{src}/{dst} | Return the desired link from the source and destination |
| AppServer/webapi/topology/terminals/        | Return all the terminals (enabled and disabled)         |
| AppServer/webapi/topology/terminals/{id}    | Return the desired terminal from its identifier         |

<h3>Statistics</h3>
<p>Resources sources availables at 'dxat.appserver.stat.resources'.</p>

<h3>Flow tables</h3>
<p>Resources source availables at 'dxat.appserver.flows.resources'</p>

| URL                                         | Description                                             |
| ------------------------------------------- |:-------------------------------------------------------:|
| AppServer/webapi/flows/                     | Return all flows (only active in the controller)        |
