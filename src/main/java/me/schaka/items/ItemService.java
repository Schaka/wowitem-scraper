package me.schaka.items;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by Jonas on 06.08.2016.
 */
@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Async
    public void saveNewOnlyAsync(ItemDocument item){
        if(!itemRepository.exists(item.getId())){
            itemRepository.save(item);
        }
    }
}
