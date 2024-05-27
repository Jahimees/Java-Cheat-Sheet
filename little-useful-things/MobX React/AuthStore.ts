import {makeAutoObservable} from "mobx";
import {User} from "../model/messenger/user";
import {GlobalUser} from "../model/local-storage/localStorageTypes";
import {StringIndexArray} from "../model/stringIndexArray";
import {touchGlobalUsers} from "./Utils";

export class AuthStore {

    // @ts-ignore
    user: User;
    welcomeModalOpen:boolean
    registrationModalOpen:boolean
    ghostRegistration:boolean
    loginModalOpen:boolean
    trustDeviceModalOpen:boolean
    editUserTitleModalOpen:boolean

    constructor() {
        makeAutoObservable(this);
        this.welcomeModalOpen = false;
        this.registrationModalOpen = false;
        this.ghostRegistration = false;
        this.loginModalOpen = false;
        this.trustDeviceModalOpen = false;
        this.editUserTitleModalOpen = false;
    }

    setWelcomeModalOpen(welcomeModalOpen:boolean) {
        this.welcomeModalOpen = welcomeModalOpen;
    }

    setLoginModalOpen(loginModalOpen:boolean) {
        this.loginModalOpen = loginModalOpen;
    }

    setUser(user:User) {
        this.user = user;
    }

    setRegistrationModalOpen(registrationModalOpen: boolean) {
        this.registrationModalOpen = registrationModalOpen;
    }
    setGhostRegistration(ghostRegistration: boolean) {
        this.ghostRegistration = ghostRegistration;
    }
    setTrustDeviceModalOpen(trustDeviceModalOpen: boolean) {
        this.trustDeviceModalOpen = trustDeviceModalOpen;
    }

    setEditUserTitleModalOpen(b: boolean) {
        this.editUserTitleModalOpen = b;
    }
}
