/*
 * Software Name : libits-client
 * SPDX-FileCopyrightText: Copyright (c) Orange SA
 * SPDX-License-Identifier: MIT
 *
 * This software is distributed under the MIT license,
 * see the "LICENSE.txt" file for more details or https://opensource.org/license/MIT/
 *
 * Authors: see CONTRIBUTORS.md
 */

use crate::exchange::mortal::Mortal;

use crate::transport::payload::Payload;
use serde::{Deserialize, Serialize};

/// Client or server information message
///
/// The message carries information about an instance involved in V2X message exchanges
/// It can be either a server hosting a broker and/or application(s) that consume/produce messages
/// or a client sending messages (OBU/RSU)
///
/// The corresponding JSON schema of this message struct can be found in this projects [schema directory][1]
///
/// [1]: https://github.com/Orange-OpenSource/its-client/tree/master/schema
#[serde_with::skip_serializing_none]
#[derive(Clone, Debug, Default, PartialEq, Serialize, Deserialize)]
pub struct Information {
    #[serde(rename = "type")]
    pub type_field: String,
    pub version: String,
    pub instance_id: String,
    pub instance_type: String,
    pub central_instance_id: Option<String>,
    pub running: bool,
    pub timestamp: u64,
    pub validity_duration: u32,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    public_ip_address: Vec<String>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    mqtt_ip: Vec<String>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    mqtt_tls_ip: Vec<String>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    http_proxy: Vec<String>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    ntp_servers: Vec<String>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    domain_name_servers: Vec<String>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    gelf_loggers: Vec<String>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    udp_loggers: Vec<String>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    fbeat_loggers: Vec<String>,
    pub service_area: Option<ServiceArea>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    cells_id: Vec<u32>,
}

#[serde_with::skip_serializing_none]
#[derive(Clone, Debug, Default, PartialEq, Serialize, Deserialize)]
pub struct ServiceArea {
    #[serde(rename = "type")]
    pub type_field: String,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    coordinates: Vec<f32>,
    radius: Option<u32>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    vertices: Vec<Vertex>,
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    pub quadkeys: Vec<String>,
}

#[serde_with::skip_serializing_none]
#[derive(Clone, Debug, Default, PartialEq, Serialize, Deserialize)]
pub struct Vertex {
    #[serde(skip_serializing_if = "Vec::is_empty", default)]
    coordinates: Vec<f32>,
}

impl Information {
    pub const TYPE: &'static str = "info";

    /// Replaces the current `Information` instance with a new one.
    ///
    /// # Arguments
    ///
    /// * `new_info` - The new `Information` instance to replace the current one.
    pub fn replace(&mut self, new_info: Information) {
        *self = new_info;
    }
}

impl Mortal for Information {
    fn timeout(&self) -> u64 {
        self.timestamp + u64::from(self.validity_duration) * 1000_u64
    }

    fn terminate(&mut self) {
        self.validity_duration = 0
    }

    fn terminated(&self) -> bool {
        self.expired()
    }
}

impl Payload for Information {}

/// A boxed type for the `Information` struct.
///
/// This is used to avoid increasing the size of enums that include `Information` as a variant.
pub type BoxedInformation = Box<Information>;

#[cfg(test)]
mod tests {
    use crate::exchange::message::information::{Information, ServiceArea};

    // FIXME either use or remove this function in tests
    #[allow(unused)]
    fn generate_central_information() -> Information {
        Information {
            instance_id: "corp_role_32".to_string(),
            service_area: Some(ServiceArea {
                quadkeys: vec!["12020".to_string()],
                ..Default::default()
            }),
            ..Default::default()
        }
    }

    // FIXME either use or remove this function in tests
    #[allow(unused)]
    fn generate_edge_information() -> Information {
        Information {
            instance_id: "corp_role_32".to_string(),
            service_area: Some(ServiceArea {
                quadkeys: vec![
                    "1202032231330103".to_string(),
                    "12020322313211".to_string(),
                    "12020322313213".to_string(),
                    "12020322313302".to_string(),
                    "12020322313230".to_string(),
                    "12020322313221".to_string(),
                    "12020322313222".to_string(),
                    "120203223133032".to_string(),
                    "120203223133030".to_string(),
                    "120203223133012".to_string(),
                    "120203223133003".to_string(),
                    "120203223133002".to_string(),
                    "120203223133000".to_string(),
                    "120203223132103".to_string(),
                    "120203223132121".to_string(),
                    "120203223132123".to_string(),
                    "120203223132310".to_string(),
                    "120203223132311".to_string(),
                    "120203223132122".to_string(),
                    "120203223132033".to_string(),
                    "120203223132032".to_string(),
                    "120203223132023".to_string(),
                    "120203223132201".to_string(),
                    "120203223132203".to_string(),
                    "120203223132202".to_string(),
                    "120203223123313".to_string(),
                    "120203223123331".to_string(),
                    "120203223123333".to_string(),
                    "120203223132230".to_string(),
                    "12020322313300133".to_string(),
                    "12020322313301022".to_string(),
                    "12020322313301023".to_string(),
                ],
                ..Default::default()
            }),
            ..Default::default()
        }
    }
}
