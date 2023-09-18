import {AuthStore} from "./AuthStore";
import {UsersStore} from "./UsersStore";
import {ChatsStore} from "./ChatsStore";
import {AudioStore} from "./AudioStore";
import {MessagesStore} from "./MessagesStore";
import {WsStore} from "./WsStore";


export class RootStore {
    authStore: AuthStore;
    usersStore: UsersStore;
    chatsStore: ChatsStore;
    audioStore: AudioStore;
    messagesStore: MessagesStore;
    wsStore: WsStore;

    constructor() {
        this.authStore = new AuthStore();
        this.usersStore = new UsersStore();
        this.chatsStore = new ChatsStore();
        this.audioStore = new AudioStore();
        this.messagesStore = new MessagesStore();
        this.wsStore = new WsStore();
    }
}

export const rootStore = new RootStore();