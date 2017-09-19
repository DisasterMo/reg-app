export class Registry {
    id: number;
    createdAt: number;
    updatedAt: number;
    registryStatus: string;
    lastAccessCheck: number;
    lastStatusChange: number;
    registryValues: any[];
    serviceId: number;
    serviceName: string;
    serviceShortDescription: string;
}
